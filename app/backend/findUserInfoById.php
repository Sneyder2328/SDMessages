<?php

$userId = $_REQUEST['userId'];
$sessionId = $_REQUEST['sessionId'];

require_once("UserInfo.php");
require_once("database.php");
require_once("sessionVerification.php");

$sessionVerified = verifySession($userId, $sessionId);

if ($userId != "" && $sessionId != "" && $sessionVerified) {
    $mysqli = mysqli();

    $result = $mysqli->query("SELECT userId,displayName,photoUrl,firebaseTokenId FROM User WHERE userId = '$userId'") or die($mysqli->error);

    if ($data = $result->fetch_array()) {
        $user = new UserInfo();
        $user->userId = $data['userId'];
        $user->displayName = $data['displayName'];
        $user->birthDate = "";
        $user->photoUrl = $data['photoUrl'];
        $user->firebaseTokenId = $data['firebaseTokenId'];
        echo json_encode($user);
    } else {
        echo json_encode("no data");
    }
    $mysqli->close();
} else {
    echo json_encode("missing params");
}