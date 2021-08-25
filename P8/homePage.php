<html>
<body>
<h1>Compiler Construction </h1>
<h1>Practical 8 </h1>
<h1>u19025638 </h1>
<button onclick=uploadFile()>Upload</button>
<form style="visibility:hidden" id ="fileForm" action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data" >
    <input type="file" name="inputFile" size="60" />
    <input type="submit" value="Process" />
</form>
<script>
function uploadFile() {
document.getElementById("fileForm").style.visibility="visible";

    }
</script>
</body>
</html>

<?php
//check is txt and not other file type
if ($_FILES) {
      if (isset($_FILES) && $_FILES['inputFile']['type'] != 'text/plain') {
          echo "<div>Please upload a .txt file only</div>";
exit();
}


//Getting file name 
$fileName = $_FILES['inputFile']['tmp_name'];

exec("java Main $fileName",$output);

foreach($output as $i){
    echo $i . "<br>";
}

//error message if unable to open
//$file = fopen($fileName,"r") or exit("Error opening file");
//outputing contents
//echo '<pre>' . file_get_contents($fileName) . '</pre>';
//saving file
 //move_uploaded_file($_FILES['inputFile']['tmp_name'], "upload/" . "result.txt");
 //flush buffer
//fclose($file);

}
?>