<?php

//We want to be able to continue if the client aborts.
ignore_user_abort();
set_time_limit(0);
error_reporting(E_ALL);
ini_set('display_errors', 'Off');
ini_set('error_log', 'errors.log');

//Abort the browser so it doesn't hang while we do the uploading.
ob_end_clean();
header("Connection: close");
ob_start();
header("Content-Length: 0");
ob_end_flush();
flush();

//Lets get to work!
include('upload.php');

$build = $_GET['buildid'];
$branch = $_GET['branch'];
$version = $_GET['version'];

include('../build/function.php');
updateval($branch);

if ($build == "" || $branch == "" || $version == "") {
  die('Invalid');
}

//Don't upload dev builds atm.
if ($branch == "bt2") {
  die();
}

sleep(60);

$changes = getChanges($build, $branch);

//uploadit($build, $branch, 'Essentials.jar', $version, $changes);
//sleep(1);
//uploadit($build, $branch, 'EssentialsChat.jar', $version, $changes);
//sleep(1);
//uploadit($build, $branch, 'EssentialsSpawn.jar', $version, $changes);
//sleep(1);
//uploadit($build, $branch, 'EssentialsProtect.jar', $version, $changes);
//sleep(1);
//uploadit($build, $branch, 'EssentialsXMPP.jar', $version, $changes);
//sleep(1);
//uploadit($build, $branch, 'EssentialsGeoIP.jar', $version, $changes);

uploadit($build, $branch, 'Essentials.zip', $version, $changes);
sleep(1);
uploadit($build, $branch, 'Essentials-extra.zip', $version, $changes);


?>

