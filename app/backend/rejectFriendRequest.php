<?php
$fromUserId = $_REQUEST['fromUserId'];
$toUserId = $_REQUEST['toUserId'];

$sessionId = $_REQUEST['sessionId'];

require_once("sessionVerification.php");
require_once("database.php");
$sessionVerified = verifySession($toUserId, $sessionId);

if ($fromUserId != "" && $toUserId != "" && $sessionId != "" && $sessionVerified) {

    $mysqli = mysqli();

    require_once("utils.php");
    $now = currentTimeInMillis();

    $mysqli->query("DELETE FROM Relationship WHERE userOneId='$fromUserId' AND userTwoId='$toUserId'") or die($mysqli->error);
    if ($mysqli->affected_rows > 0) {
        echo "true";
    } else {
        echo "error deleting  Relationship";
    }
    $mysqli->close();
} else {
    echo "missing arguments";
}

?>
