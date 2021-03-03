<?php
$name = $_REQUEST['name'];
require_once("UserInfo.php");
require_once("database.php");

if ($name!="") {
  $mysqli = mysqli();

  $result = $mysqli->query("SELECT userId,displayName,photoUrl,firebaseTokenId FROM `User` WHERE displayName LIKE '$name%'") or die($mysqli->error);
  if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()){
      $user = new UserInfo();
      $user->userId = $row['userId'];
      $user->displayName = $row['displayName'];
      $user->birthDate = "";
      $user->photoUrl = $row['photoUrl'];
      $user->firebaseTokenId = $row['firebaseTokenId'];
      $users[] = $user;
    }
    echo json_encode($users);
  }
  else {
    echo json_encode("no data");
  }
  $mysqli->close();
}
else {
  echo json_encode("missing params");
}