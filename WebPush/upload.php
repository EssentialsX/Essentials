<?php

include_once('simple_html_dom.php');

function uploadit($build, $branch, $file, $version, $changes) {
  file_put_contents('status.log', "\nUploading file $file to devbukkit! ", FILE_APPEND);
  $slug = "essentials";
  $plugin = "Essentials";
  $url = "http://ci.earth2me.net/guestAuth/repository/download/$branch/$build:id/$file";
  $filename = explode('.', $file);
  $request_url = "http://dev.bukkit.org/server-mods/$slug/upload-file.json";

  include ('apikey.php');

  $params['name'] = $filename[0] . '-' . $version;
  $params['game_versions'] = 176;
  $params['change_log'] = $changes;
  $params['change_markup_type'] = "html";
  $params['fileurl'] = $url;

  if (stripos($version, 'Dev') !== false) {
    $params['file_type'] = "a";
  }
  elseif (stripos($version, 'Pre') !== false) {
    $params['file_type'] = "b";
  }
  else {
    $params['file_type'] = "r";
  }

  $content = file_get_contents($url);
  file_put_contents($file, $content);

  $params['file'] = '@' . $file;

  $ch = curl_init();
  curl_setopt($ch, CURLOPT_URL, $request_url);
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
  curl_setopt($ch, CURLOPT_POST, true);
  curl_setopt($ch, CURLOPT_POSTFIELDS, $params);
  $result = curl_exec($ch);

  if ($result === false) {
    $result = curl_error($ch);
  }
  elseif ($result == "") {
    $result = "Success uploading $file - $version";
  }
  curl_close($ch);

  file_put_contents('status.log', $result, FILE_APPEND);
  return true;
}

function getChanges($job, $project) {
  $commitblacklist = array(
      'Merge branch',
      'Merge pull',
      'Revert',
      'Cleanup',
  );

  $url = "http://ci.earth2me.net/viewLog.html?buildId=$job&tab=buildChangesDiv&buildTypeId=$project&guest=1";

  $html = new simple_html_dom();
  $html->load_file($url);

  $output = "Change Log:<ul>";
  foreach ($html->find('.changelist') as $list) {
    foreach ($list->find('.comment') as $comment) {
      $text = $comment->innertext;
      foreach ($commitblacklist as $matchtext) {
        if (stripos($text, $matchtext) !== FALSE) {
          $text = "";
        }
      }
      if ($text != "") {
        $output .= "<li>$text</li>\n";
      }
    }
  }
  $output .= "</ul>";

  file_put_contents('status.log', "Collected changes! ", FILE_APPEND);

  return $output;
}
?>

