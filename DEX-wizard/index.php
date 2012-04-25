<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
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
      $(document).ready(onDocumentReady);
      
      function onDocumentReady() {
        updateAll();
        setInterval(updateLast, 500);
      }
      
      function save(item) {
        var status = $('#'+item).prop('checked') ? 1 : 0;
        $.get("write.php?thingy=" + item + "&status=" + status);
      }
    
      function updateAll() {
        for (i = 0; i < 3; i++) {
          updateItem('status_' + i);
          updateItem('scan_' + i);
        }
      }
      
      function updateLast() {
        $.getJSON('last', function(data) {
          if (data != null) {
            $('#' + data[0]).prop('checked', data[1] == "1");
            $.get('write.php?clear=1');
          }
        });
      }
      
      function updateItem(item) {
        $.get(item, function(data) {
          $('#' + item).prop('checked', data == "1");
        });
      }
      
    </script>
  </head>
  <body>
    <form id="form" action="" method="post">
      <div>
        <input type="checkbox" name="scan_0" id="scan_0" value="1" onchange="save('scan_0')" />
        <label for="scan_0">Thingy 1 Scanned</label><br />
        
        <input type="checkbox" name="scan_1" id="scan_1" value="1" onchange="save('scan_1')" />
        <label for="scan_1">Thingy 2 Scanned</label><br />
        
        <input type="checkbox" name="scan_2" id="scan_2" value="1" onchange="save('scan_2')" />
        <label for="scan_2">Thingy 3 Scanned</label><br />
        <br />

        <input type="checkbox" name="status_0" id="status_0" value="1" onchange="save('status_0')" />
        <label for="status_0">Thingy 1 On</label><br />
        
        <input type="checkbox" name="status_1" id="status_1" value="1" onchange="save('status_1')" />
        <label for="status_1">Thingy 2 On</label><br />

        <input type="checkbox" name="status_2" id="status_2" value="1" onchange="save('status_2')" />
        <label for="status_2">Thingy 3 On</label><br />
      </div>
    </form>
  </body>
</html>