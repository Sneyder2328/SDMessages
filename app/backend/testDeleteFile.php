<?php
//require '/home/ubuntu/aws/aws-autoloader.php';
//require '/home/ubuntu/aws.phar';
require '/home/ubuntu/vendor/autoload.php';

$image = $_REQUEST['image'];

$bucket = substr($image, 0, strpos($image, '---'));
$keyname = substr($image, strpos($image, '---')+3);

$sharedConfig = [
    'region'  => 'us-east-1',
    'version' => 'latest',
    'credentials' => [
        'key'    => 'AKIAJUI7KS4WM6UVWG5A',
        'secret' => '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'
    ]
];
// Create an SDK class used to share configuration across clients.
$sdk = new Aws\Sdk($sharedConfig);
$s3 = $sdk->createS3();
/*
$bucket = 'sdmessages-userfiles-mobilehub-100489725';
$keyname = 'public/SDMessages_20180112_091654.jpg';
*/
try {
    $result = $s3->deleteObject(array(
        'Bucket' => $bucket,
        'Key'    => $keyname
    ));
    echo "done";
} catch (Aws\S3\Exception\S3Exception $e) {
    echo "There was an error deleting the file.\n";
}