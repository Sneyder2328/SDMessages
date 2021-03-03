<?php
$groupId = $_POST['groupId'];
$name = $_POST['name'];
$adminId = $_POST['adminId'];
$adminSessionId = $_POST['adminSessionId'];
$typeAccess = $_POST['typeAccess'];
$password = $_POST['password'];
if(isset($_POST['pictureUrl'])){
    $pictureUrl = $_POST['pictureUrl'];
}
else {
    $pictureUrl = "";
}

require_once("sessionVerification.php");
require_once("database.php");
require_once("GroupInfo.php");
require_once("utils.php");
$sessionVerified = verifySession($adminId, $adminSessionId);

if ($groupId != "" && $name != "" && $adminId != "" && $adminSessionId != "" && $sessionVerified && $typeAccess!="") {
    $now = currentTimeInMillis();

    $mysqli = mysqli();

    $mysqli->query("INSERT INTO `Group`(groupId, `name`, adminId, dateCreated, pictureUrl, typeAccess) VALUES('$groupId', '$name', '$adminId', $now, '$pictureUrl', '$typeAccess')") or die($mysqli->error);
    if ($mysqli->affected_rows > 0) {
        $mysqli->query("INSERT INTO UserGroup(userId, groupId, status, lastInteraction) VALUES('$adminId', '$groupId', 'Member', $now)") or die($mysqli->error);
        $group = new GroupInfo();
        $group->groupId = $groupId;
        $group->name = $name;
        $group->adminId = $adminId;
        $group->password = $password;
        $group->typeAccess = $typeAccess;
        $group->pictureUrl = $pictureUrl;
        echo json_encode($group);
    } else {
        echo "false";
    }
    $mysqli->close();
} else {
    echo "false";
}