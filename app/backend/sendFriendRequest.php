<?php
$server_key = 'AIzaSyAD6DQOXHfjtD-CO07eGVbT8tTtaFjf20M';
$headers = array('Content-Type:application/json', 'Authorization:key='.$server_key);

$myUserId = $_POST['userId'];
$sessionId = $_POST['sessionId'];
$otherFirebaseTokenId = $_POST['otherFirebaseTokenId'];
$otherUserId = $_POST['otherUserId'];
$message = $_POST['message'];

require_once("sessionVerification.php");
$sessionVerified = verifySession($myUserId, $sessionId);

if ($myUserId!="" && $sessionId!="" && $otherFirebaseTokenId!="" && $otherUserId!="" && $sessionVerified) {

    require_once("database.php");
    $mysqli = mysqli();

    $mysqli->query("INSERT INTO Relationship(userOneId, userTwoId, status) VALUES('$myUserId', '$otherUserId', 'PendingRequest')") or die($mysqli->error);
    if ($mysqli->affected_rows > 0) {

      $reg = $mysqli->query("SELECT displayName,photoUrl,firebaseTokenId FROM User WHERE userId='$myUserId'") or die($mysqli->error);

      if($myUserData = $reg->fetch_array()){
        $data = array(
          "notificationType" => "friendRequest",
          "fromUserId" => $myUserId,
          "fromUserName" => $myUserData['displayName'],
          "fromPhotoUrl" => $myUserData['photoUrl'],
          "fromFirebaseTokenId" => $myUserData['firebaseTokenId'],
          "toUserId" => $otherUserId,
          "message" => $message);

        $url = 'https://fcm.googleapis.com/fcm/send';

        $fields = array();
        $fields['data'] = $data;
        $fields['to'] = $otherFirebaseTokenId;
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
          echo "FCM message sent";
        }
        else {
          echo "FCM message not sent";
        }
      }
      else {
        echo "no data for myUserId=".$myUserId;
      }

    }
    else {
      echo "error inserting Relationship";
    }
    $mysqli->close();
}
else  {
  echo "missing arguments";
}