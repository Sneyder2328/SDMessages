<?php
$password = 'KRXKO1JI9frauBPEqQ0gPKabmtRAnxYY95vQHgMENfg=';
$hashedPassword = password_hash($password, PASSWORD_DEFAULT, ['cost' => 8]);
if (password_verify($password, $hashedPassword)) {
  echo "valid ".$hashedPassword;
}
else {
  echo "invalid";
}
 ?>
