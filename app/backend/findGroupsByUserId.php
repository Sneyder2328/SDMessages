<?php
$userId = $_REQUEST['userId'];
$sessionId = $_REQUEST['sessionId'];

require_once("GroupInfo.php");
require_once("sessionVerification.php");
require_once("database.php");
$sessionVerified = verifySession($userId, $sessionId);

if ($userId != "" && $sessionId != "" && $sessionVerified) {
    $mysqli = mysqli();
    $result = $mysqli->query("SELECT groupId FROM UserGroup WHERE userId='$userId' AND status='Member' ORDER BY lastInteraction DESC") or die($mysqli->error);
    if ($result->num_rows > 0) {

        while ($row = $result->fetch_assoc()) {
            $groupId = $row['groupId'];

            $resultGroupInfo = $mysqli->query("SELECT `name`, pictureUrl FROM `Group` WHERE groupId='$groupId' LIMIT 1") or die($mysqli->error);
            if ($data = $resultGroupInfo->fetch_array()) {
                $group = new GroupInfo();                $group->groupId = $groupId;
                $group->name = $data['name'];
                $group->pictureUrl = $data['pictureUrl'];
                $group->adminId = "";
                $group->typeAccess = ""; // I don't care about the typeAccess and password of the groups I'm in
                $group->password = "";
                $groups[] = $group;
            }
        }
        $mysqli->close();
        echo json_encode($groups);
    }

} else {
    echo json_encode("missing userId param or invalid session verification, error");
}
