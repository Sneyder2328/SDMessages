<?php
function request($method, $url, $header, $params) {
    $opts = array(
        'http' => array(
            'method' => $method,
        ),
    );
    // serialize the header if needed
    if (!empty($header)) {
        $header_str = '';
        foreach ($header as $key => $value) {
            $header_str .= "$key: $value\r\n";
        }
        $header_str .= "\r\n";
        $opts['http']['header'] = $header_str;
    }
    // serialize the params if there are any
    if (!empty($params)) {
        $params_array = array();
        foreach ($params as $key => $value) {
            $params_array[] = "$key=$value";
        }
        $url .= '?'.implode('&', $params_array);
    }
    $context = stream_context_create($opts);
    $data = file_get_contents($url, false, $context);
    return $data;
}

function verifyTokenId($userId, $typeLogin, $accessToken) {
  if($typeLogin=="Facebook"){
    $appAccessToken = "158152681487301|xQmAWuwi1ZGF_QBB4egZZCGh1YQ";
    $appId = "158152681487301";
    $application = "SDMessages";

    $url = 'https://graph.facebook.com/debug_token';
    $params = array('input_token' => $accessToken, 'access_token' => $appAccessToken);
    $header = array('Content-Type' => 'application/json');
    $response = json_decode(request("GET", $url, $header, $params), true);
    $responseAppId = $response['data']["app_id"];
    $responseIsValid = (bool) $response['data']["is_valid"];
    $responseApplication = $response['data']["application"];
    $responseUserId = $response['data']["user_id"];

    if($responseIsValid == true && $appId == $responseAppId && $userId == $responseUserId && $application == $responseApplication){
      return true; //
    }
    else {
      return false; // invalid token or data does not match
    }
  }
  if ($typeLogin=="Google") {
    require_once '/home/ubuntu/vendor/autoload.php'; // Required for Google Sign in
    $CLIENT_ID = "434477538698-8ulu0utc36ugu3ob252o5bptt1s52clk.apps.googleusercontent.com";
    $client = new Google_Client(['client_id' => $CLIENT_ID]);
    $payload = $client->verifyIdToken($accessToken);
    if ($payload) {
      $responseUserId = $payload['sub'];
      if ($responseUserId == $userId) {
        return true;
      }
      else {
        return false; // userId does not match
      }
    }
    else {
      return false;// Invalid ID token
    }
  }
  return false;
}