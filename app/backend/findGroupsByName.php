<?php
$name = $_REQUEST['name'];
require_once("GroupInfo.php");
require_once("database.php");

if ($name!="") {
    $mysqli = mysqli();

    $result = $mysqli->query("SELECT groupId,`name`,pictureUrl,typeAccess FROM `Group` WHERE `name` LIKE '$name%'") or die($mysqli->error);
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()){
            $group = new GroupInfo();
            $group->groupId = $row['groupId'];
            $group->name = $row['name'];
            $group->pictureUrl = $row['pictureUrl'];
            $group->typeAccess = $row['typeAccess'];
            $groups[] = $group;
        }
        echo json_encode($groups);
    }
    else {
        echo json_encode("no data");
    }
    $mysqli->close();
}
else {
    echo json_encode("missing params");
}