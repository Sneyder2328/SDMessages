<?php
//ini_set('display_errors', 1);
//error_reporting(E_ALL);
require_once("utils.php");
require_once("tokenVerification.php");
require_once("database.php");

class UserInfo
{
    public $userId = "";
    public $displayName = "";
    public $birthDate = "";
    public $photoUrl = "";
    public $sessionId = "";
    public $firebaseTokenId = "";
}

function hashEmail($emailToHash)
{
    return hash_pbkdf2("sha256", $emailToHash, "maupnennehimgbtfbtbd", 200, 20);
}

$email = $_REQUEST['email'];
$typeLogin = $_REQUEST['typeLogin'];
$password = $_REQUEST['password'];

$verified = true;
if ($typeLogin == "Facebook" || $typeLogin == "Google") {
    $accessToken = $_REQUEST['accessToken'];
    $verified = verifyTokenId($_REQUEST['userId'], $typeLogin, $accessToken);
}

if ($email != "" && $verified == true) {
    $mysqli = mysqli();

    $hashedEmail = hashEmail($email);

    $result = $mysqli->query("SELECT password, userId, displayName, birthDate, photoUrl FROM User WHERE email='$hashedEmail' LIMIT 1") or die($mysqli->error);

    if ($data = $result->fetch_array()) {
        $hashedPassword = $data['password'];
        $userId = $data['userId'];
        if (password_verify($password, $hashedPassword)) {

            $sessionId = bin2hex(random_bytes(50));
            $day = 86400000; // milliseconds a day has
            $expiryDate = currentTimeInMillis() + ($day * 60); // currentTimeInMillis + 60 days
            $mysqli->query("INSERT INTO Session(sessionId, userId, expiryDate) VALUES('$sessionId', '$userId', $expiryDate) ON DUPLICATE KEY UPDATE expiryDate = '$expiryDate', sessionId = '$sessionId'") or die($mysqli->error);
            $mysqli->close();

            $user = new UserInfo();
            $user->userId = $userId;
            $user->displayName = $data['displayName'];
            $user->birthDate = $data['birthDate'];
            $user->photoUrl = $data['photoUrl'];
            $user->sessionId = $sessionId;
            $user->firebaseTokenId = $data['firebaseTokenId'];
            echo json_encode($user);
        } else {
            echo "wrong password";
        }
    }

} else {
    echo "missing arguments or invalid auth token";
}