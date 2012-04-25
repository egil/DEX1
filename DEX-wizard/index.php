<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<?php

  /* 
    Quick interface for simulating scanning for, and changing values of, simple thingies.
     Excuse the mess, this was a quick one! 
  */

  $status1 = $_POST['status1'];
  $status2 = $_POST['status2'];
  $scan1 = $_POST['scan1'];
  $scan2 = $_POST['scan2'];

  $f1 = fopen("one", 'w');
  fwrite($f1, $status1);
  fclose($f1);

  $f2 = fopen("two", 'w');
  fwrite($f2, $status2);
  fclose($f2);
  
  $f3 = fopen("scan_one", 'w');
  fwrite($f3, $scan1);
  fclose($f3);
  
  $f4 = fopen("scan_two", 'w');
  fwrite($f4, $scan2);
  fclose($f4);

?>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Wizard interface</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <meta name="viewport" content="width=device-width">
    <style type="text/css">
      body, input
      { 
        font-family: Trebuchet MS;
      }
      body
      {
        margin: 8%;
      }
    </style>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script type="text/javascript">
      function submitForm() {
        $.post("index.php", $('#form').serialize(), null, "script");
      }
    </script>
  </head>
  <body>
    <form id="form" action="" method="post">
      <div>
        <input type="checkbox" name="scan1" id="scan1" value="1" onchange="submitForm()" />
        <label for="scan1">Thingy 1 Scanned</label><br />

        <input type="checkbox" name="scan2" id="scan2" value="1" onchange="submitForm()" />
        <label for="scan2">Thingy 2 Scanned</label><br />
        <br />

        <input type="checkbox" name="status1" id="status1" value="1" onchange="submitForm()" />
        <label for="status1">Thingy 1 On</label><br />

        <input type="checkbox" name="status2" id="status2" value="1" onchange="submitForm()" />
        <label for="status2">Thingy 2 On</label><br />
      </div>
    </form>
  </body>
</html>