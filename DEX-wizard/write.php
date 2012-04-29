<?php

$filename = $_GET['thingy'];
$status = $_GET['status'];
$setLast = $_GET['setLast'];
$clear = $_GET['clear'];


if ($clear) {
  $fc = fopen('last', 'w');
  fclose($fc);
  die('cleared');
}

if (strpos($filename, ".") !== false)
  die('No!');
else if (strpos($filename, "\\") !== false)
  die('No!');

if ($filename != "") {
  $f = fopen($filename, 'w');
  fwrite($f, $status);
  fclose($f);
}

if ($setLast) {
  $flast = fopen('last', 'w');
  fwrite($flast, "[ \"$filename\", \"$status\" ]");
  fclose($flast);
}

echo "Done";

?>