<?php
$userId = $_REQUEST['userId'];
$sessionId = $_REQUEST['sessionId'];
$friendUserId = $_REQUEST['friendUserId'];

require_once("sessionVerification.php");
require_once("database.php");
require_once("Message.php");

$sessionVerified = verifySession($userId, $sessionId);

if ($userId != "" && $friendUserId != "" && $sessionId != "" && $sessionVerified) {

    $mysqli = mysqli();
    $result = $mysqli->query("SELECT messageId,content,senderId,recipientId,typeContent,dateCreated,dateExpiry FROM Message WHERE (senderId = '$friendUserId' AND recipientId='$userId') OR (senderId = '$userId' AND recipientId='$friendUserId')") or die($mysqli->error);

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $message = new Message();
            $message->messageId = $row['messageId'];
            $message->content = $row['content'];
            $message->senderId = $row['senderId'];
            $message->recipientId = $row['recipientId'];
            $message->typeContent = $row['typeContent'];
            $message->dateCreated = $row['dateCreated'];
            $message->dateExpiry = $row['dateExpiry'];
            $messages[] = $message;
        }
        echo json_encode($messages);
    } else {
        echo "no messages found";
    }
    $mysqli->close();
} else {
    echo "missing some arguments";
}