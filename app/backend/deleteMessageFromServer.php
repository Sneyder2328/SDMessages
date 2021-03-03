<?php

$userId = $_POST['userId'];
$sessionId = $_POST['sessionId'];
$friendUserId = $_POST['friendUserId'];
$lastMessageViewedDate = $_POST['lastMessageViewedDate'];

require_once("sessionVerification.php");
require_once("database.php");

$sessionVerified = verifySession($userId, $sessionId);

if ($userId!="" && $sessionId!="" && $friendUserId!="" && $lastMessageViewedDate!="" && $sessionVerified) {

    $mysqli = mysqli();

    $mysqli->query("DELETE FROM Message WHERE senderId = '$friendUserId' AND recipientId='$userId' AND dateCreated<=$lastMessageViewedDate") or die($mysqli->error);

    if ($mysqli->affected_rows > 0) {
        echo "true";
    }
    else {
        echo "error deleting messages";
    }
    $mysqli->close();
}
else {
    echo "missing arguments";
}