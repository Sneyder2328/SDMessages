<?php
$server_key = 'AIzaSyAD6DQOXHfjtD-CO07eGVbT8tTtaFjf20M';
$headers = array('Content-Type:application/json', 'Authorization:key=' . $server_key);

$senderId = $_POST['senderId'];
$sessionId = $_POST['sessionId'];
$recipientId = $_POST['recipientId'];
$content = $_POST['content'];
$typeContent = $_POST['typeContent'];

require_once("sessionVerification.php");
$sessionVerified = verifySession($senderId, $sessionId);

if ($senderId != "" && $recipientId != "" && $sessionId != "" && $sessionVerified && $content != "" && $typeContent != "") {

    require_once("database.php");

    $mysqli = mysqli();

    require_once("utils.php");
    $now = currentTimeInMillis();
    $dateExpiry = $now + (5 * 1000);

    $mysqli->query("INSERT INTO Message(content,senderId,recipientId,typeContent,dateCreated,dateExpiry) VALUES('$content', '$senderId', '$recipientId', '$typeContent', $now, $dateExpiry)") or die($mysqli->error);
    if ($mysqli->affected_rows > 0) {
        echo $now; // It's important to return the dateCreated as this page response
        $resultRecipient = $mysqli->query("SELECT firebaseTokenId FROM User WHERE userId='$recipientId'") or die($mysqli->error);

        if ($recipientData = $resultRecipient->fetch_array()) {
            $recipientFirebaseTokenId = $recipientData['firebaseTokenId'];
        } else {
            echo "error getting the recipient firebaseTokenId";
            $mysqli->close();
            return;
        }

        $resultSender = $mysqli->query("SELECT displayName,photoUrl FROM `User` WHERE userId='$senderId'") or die($mysqli->error);

        if ($senderData = $resultSender->fetch_array()) {
            $data = array(
                "notificationType" => "newMessage",
                "fromUserId" => $senderId,
                "fromUserName" => $senderData['displayName'],
                "fromPhotoUrl" => $senderData['photoUrl'],
                /*"fromFirebaseTokenId" => $senderData['firebaseTokenId'],*/
                "fromGroup" => "false",
                "toUserId" => $recipientId,
                "content" => $content,
                "typeContent" => $typeContent);

            $url = 'https://fcm.googleapis.com/fcm/send';

            $fields = array();
            $fields['data'] = $data;
            $fields['to'] = $recipientFirebaseTokenId;
            $fields['priority'] = "high";

            $ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

            $curlResult = json_decode(curl_exec($ch), true);
            curl_close($ch);
            $success = $curlResult['success'];
            if ($success > 0) {
               // echo "FCM message sent";
            } else {
               // echo "FCM message not sent";
            }
        } else {
            echo "no data for senderId=" . $senderId;
        }
    } else {
        echo "error inserting Message";
    }
    $mysqli->close();
} else {
    echo "missing arguments";
}