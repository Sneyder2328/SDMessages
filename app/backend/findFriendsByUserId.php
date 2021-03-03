<?php
$userId = $_REQUEST['userId'];
$sessionId = $_REQUEST['sessionId'];

require_once("sessionVerification.php");
require_once("database.php");
require_once("UserInfo.php");

$sessionVerified = verifySession($userId, $sessionId);

if ($userId != "" && $sessionVerified == true) {
    $mysqli = mysqli();
    $result = $mysqli->query("SELECT relationshipId,userOneId,userTwoId FROM Relationship WHERE (userOneId='$userId' OR userTwoId='$userId') AND status='Friends' ORDER BY lastInteraction DESC") or die($mysqli->error);

    if ($result->num_rows > 0) {

        while ($row = $result->fetch_assoc()) {
            $userOneId = $row['userOneId'];
            $userTwoId = $row['userTwoId'];

            if ($userOneId == $userId) {
                $friendUserId = $userTwoId;
            } else {
                $friendUserId = $userOneId;
            }

            $resultUserInfo = $mysqli->query("SELECT displayName, photoUrl, firebaseTokenId FROM User WHERE userId='$friendUserId' LIMIT 1") or die($mysqli->error);
            if ($data = $resultUserInfo->fetch_array()) {
                $user = new UserInfo();
                $user->userId = $friendUserId;
                $user->displayName = $data['displayName'];
                $user->birthDate = "";
                $user->typeUser = "Friend";
                $user->photoUrl = $data['photoUrl'];
                $user->firebaseTokenId = $data['firebaseTokenId'];
                $users[] = $user;
            }

        }
        $mysqli->close();
        echo json_encode($users);
    }

} else {
    echo json_encode("missing userId param or invalid verification");
}