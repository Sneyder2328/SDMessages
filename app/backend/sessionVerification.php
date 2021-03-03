<?php
require_once("utils.php");
require_once("database.php");

function verifySession($userId, $sessionId){
  if ($userId!="" && $sessionId!="") {
    $mysqli = mysqli();
    $currentTimeInMillis = currentTimeInMillis();
    $reg = $mysqli->query("SELECT * FROM Session WHERE userId='$userId' AND sessionId='$sessionId' AND expiryDate >= $currentTimeInMillis") or die($mysqli->error);
    //$reg = $mysqli->query("SELECT * FROM Session WHERE sessionId='$sessionId'") or die($mysqli->error);
    if($data = $reg->fetch_array()){
      return true;
    }
    else {
      return false;
    }
  }
  else {
    return false;
  }
}
