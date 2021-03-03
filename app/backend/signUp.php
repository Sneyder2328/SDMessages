<?php
require_once("utils.php");
require_once("database.php");
require_once("tokenVerification.php");

class UserInfo
{
    public $userId = "";
    public $displayName = "";
    public $birthDate = "";
    public $photoUrl = "";
    public $firebaseTokenId = "";
    public $sessionId = "";
}

function hashEmail($emailToHash)
{
    return hash_pbkdf2("sha256", $emailToHash, "maupnennehimgbtfbtbd", 200, 20);
}

$userId = $_POST['userId'];
$displayName = $_POST['displayName'];
$emailPlain = $_POST['email'];
$email = hashEmail($emailPlain);
$passwordPlain = $_POST['password'];
$typeLogin = $_POST['typeLogin'];
$birthDate = $_POST['birthDate'];
$photoUrl = $_POST['photoUrl'];


$firebaseTokenId = isset($_REQUEST['firebaseTokenId']) ? $_REQUEST['firebaseTokenId'] : "";

if ($passwordPlain != "") {
    $password = password_hash($passwordPlain, PASSWORD_DEFAULT, ['cost' => 8]);
} else {
    $password = "";
}
$verified = true;
if ($typeLogin == "Facebook" || $typeLogin == "Google") {
    $accessToken = $_REQUEST['accessToken'];
    $verified = verifyTokenId($userId, $typeLogin, $accessToken);
}
if ($userId != "" && $email != "" && $displayName != "" && $typeLogin != "" && $birthDate != "" && $verified) {
    $mysqli = mysqli();

    $user = new UserInfo();
    if ($photoUrl != "") {
        $mysqli->query("INSERT INTO User(userId, displayName, email, password, typeLogin, birthDate, firebaseTokenId, photoUrl)
    VALUES('$userId', '$displayName', '$email', '$password', '$typeLogin', $birthDate, '$firebaseTokenId', '$photoUrl')") or die($mysqli->error);
        $user->photoUrl = $photoUrl;
    } else {
        $mysqli->query("INSERT INTO User(userId, displayName, email, password, typeLogin, birthDate, firebaseTokenId)
    VALUES('$userId', '$displayName', '$email', '$password', '$typeLogin', $birthDate, '$firebaseTokenId')") or die($mysqli->error);
        $user->photoUrl = "";
    }
    $sessionId = bin2hex(random_bytes(50));
    $day = 86400000; // milliseconds a day has
    $expiryDate = currentTimeInMillis() + ($day * 60); // currentTimeInMillis + 60 days
    $mysqli->query("INSERT INTO Session(sessionId, userId, expiryDate) VALUES('$sessionId', '$userId', $expiryDate)") or die($mysqli->error);

    $mysqli->close();
    $user->userId = $userId;
    $user->displayName = $displayName;
    $user->birthDate = $birthDate;
    $user->sessionId = $sessionId;
    echo json_encode($user);
} else {
    echo json_encode("missing arguments or failed verification");
}