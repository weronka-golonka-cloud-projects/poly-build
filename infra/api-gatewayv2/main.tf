resource "aws_apigatewayv2_api" "poly_build_api_gateway" {
  name = "poly-build-api-gateway"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "dev" {
  api_id = aws_apigatewayv2_api.poly_build_api_gateway.id
  auto_deploy = true
  name = "$default"
}

resource "aws_apigatewayv2_integration" "poly_build_api_integration" {
  api_id           = aws_apigatewayv2_api.poly_build_api_gateway.id
  integration_type = "AWS_PROXY"
  integration_uri = var.poly_build_lambda_arn
  payload_format_version = "2.0"
}

resource "aws_apigatewayv2_route" "openapi_endpoint" {
  api_id = aws_apigatewayv2_api.poly_build_api_gateway.id
  route_key = "GET /openapi.json"
  target = "integrations/${aws_apigatewayv2_integration.poly_build_api_integration.id}"
}

resource "aws_apigatewayv2_route" "split_endpoint" {
  api_id    = aws_apigatewayv2_api.poly_build_api_gateway.id
  route_key = "POST /split"
  target = "integrations/${aws_apigatewayv2_integration.poly_build_api_integration.id}"
}