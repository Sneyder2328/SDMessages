<?php
$userId = $_POST['userId'];
$sessionId = $_POST['sessionId'];
$photoUrl = $_POST['photoUrl'];

require_once("sessionVerification.php");
require_once("database.php");

$sessionVerified = verifySession($userId, $sessionId);

if ($userId!="" && $sessionId!="" && $sessionVerified && $photoUrl!="") {
    $mysqli = mysqli();

    $mysqli->query("UPDATE User SET photoUrl = '$photoUrl' WHERE userId = '$userId'") or die($mysqli->error);
    if ($mysqli->affected_rows > 0) {
        echo "true";
    }
    else {
        echo "false";
    }
    $mysqli->close();
}
else {
    echo "false";
}