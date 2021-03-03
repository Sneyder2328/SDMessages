<?php

$server_key = 'AIzaSyAD6DQOXHfjtD-CO07eGVbT8tTtaFjf20M';
$headers = array('Content-Type:application/json', 'Authorization:key=' . $server_key);

$senderId = $_REQUEST['senderId'];
$sessionId = $_REQUEST['sessionId'];
$recipientGroupId = $_REQUEST['recipientGroupId'];
$content = $_REQUEST['content'];
$typeContent = $_REQUEST['typeContent'];

require_once("database.php");
require_once("sessionVerification.php");
require_once("utils.php");

$sessionVerified = verifySession($senderId, $sessionId);

if ($senderId != "" && $recipientGroupId != "" && $sessionId != "" && $sessionVerified && $content != "" && $typeContent != "") {

    $mysqli = mysqli();
    $now = currentTimeInMillis();
    $dateExpiry = $now + (5 * 1000);

    $mysqli->query("INSERT INTO Message(content,senderId,recipientGroupId,typeContent,dateCreated,dateExpiry) VALUES('$content', '$senderId', '$recipientGroupId', '$typeContent', $now, $dateExpiry)") or die($mysqli->error);
    if ($mysqli->affected_rows > 0) {
        echo $now; // It's important to return the dateCreated as this page response

        $resultGroupMembersIds = $mysqli->query("SELECT userID FROM UserGroup WHERE groupId='$recipientGroupId'") or die($mysqli->error);

        if ($groupMembersIds = $resultGroupMembersIds->fetch_array()) {
            $userId = $groupMembersIds['userId'];
            if ($senderId != $userId) {
                $recipientsId[] = $userId;
            }
        }

        foreach ($recipientsId as $recipientId) {
            $resultRecipient = $mysqli->query("SELECT firebaseTokenId FROM `User` WHERE userId='$recipientId'") or die($mysqli->error);

            if ($recipientData = $resultRecipient->fetch_array()) {
                $recipientFirebaseTokenIds[] = $recipientData['firebaseTokenId'];
            }
        }

        if (!empty($recipientFirebaseTokenIds)) {
            $resultGroup = $mysqli->query("SELECT `name`,pictureUrl FROM `Group` WHERE groupId='$recipientGroupId'") or die($mysqli->error);

            if ($groupData = $resultGroup->fetch_array()) {
                $data = array(
                    "notificationType" => "newMessage",
                    "fromUserId" => $senderId,
                    "fromUserName" => $senderData['name'],
                    "fromPhotoUrl" => $senderData['pictureUrl'],
                    /*"fromFirebaseTokenId" => $senderData['firebaseTokenId'],*/
                    "fromGroup" => "true",
                    "content" => $content,
                    "typeContent" => $typeContent);

                $url = 'https://fcm.googleapis.com/fcm/send';

                $fields = array();
                $fields['data'] = $data;
                $fields['registration_ids'] = $recipientFirebaseTokenIds;
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
        }

    } else {
        echo "error inserting Message";
    }
    $mysqli->close();
} else {
    echo "missing arguments";
}