<?php
$fromUserId = $_POST['fromUserId'];
$toUserId = $_POST['toUserId'];

$sessionId = $_POST['sessionId'];

require_once("sessionVerification.php");
$sessionVerified = verifySession($toUserId, $sessionId);

if ($fromUserId!="" && $toUserId!="" && $sessionId!="" && $sessionVerified) {

  require_once("database.php");

  $mysqli = mysqli();

  require_once("utils.php");
  $now = currentTimeInMillis();

  $mysqli->query("UPDATE Relationship SET status = 'Friends', lastInteraction=$now WHERE userOneId='$fromUserId' AND userTwoId='$toUserId'") or die($mysqli->error);
  if ($mysqli->affected_rows > 0) {
    echo "true";
  }
  else {
    echo "error updating Relationship";
  }
  $mysqli->close();
}
else {
  echo "missing arguments";
}