resource "aws_api_gateway_rest_api" "poly_build_api_gateway" {
  name = "poly-build-api-gateway"
}

resource "aws_api_gateway_resource" "split_endpoint" {
  rest_api_id = aws_api_gateway_rest_api.poly_build_api_gateway.id
  parent_id   = aws_api_gateway_rest_api.poly_build_api_gateway.root_resource_id
  path_part    = "split"
}

resource "aws_api_gateway_resource" "openapi_endpoint" {
  rest_api_id = aws_api_gateway_rest_api.poly_build_api_gateway.id
  parent_id   = aws_api_gateway_rest_api.poly_build_api_gateway.root_resource_id
  path_part    = "openapi.json"
}

resource "aws_api_gateway_method" "split_post" {
  rest_api_id   = aws_api_gateway_rest_api.poly_build_api_gateway.id
  resource_id   = aws_api_gateway_resource.split_endpoint.id
  http_method   = "POST"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "split_post_integration" {
  rest_api_id             = aws_api_gateway_rest_api.poly_build_api_gateway.id
  resource_id             = aws_api_gateway_resource.split_endpoint.id
  http_method             = aws_api_gateway_method.split_post.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "arn:aws:apigateway:${var.aws_region}:lambda:path/2015-03-31/functions/${var.poly_build_lambda_arn}/invocations"
}

resource "aws_api_gateway_method" "openapi_get" {
  rest_api_id   = aws_api_gateway_rest_api.poly_build_api_gateway.id
  resource_id   = aws_api_gateway_resource.openapi_endpoint.id
  http_method   = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "openapi_get_integration" {
  rest_api_id             = aws_api_gateway_rest_api.poly_build_api_gateway.id
  resource_id             = aws_api_gateway_resource.openapi_endpoint.id
  http_method             = aws_api_gateway_method.openapi_get.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "arn:aws:apigateway:${var.aws_region}:lambda:path/2015-03-31/functions/${var.poly_build_lambda_arn}/invocations"
}

resource "aws_api_gateway_deployment" "poly_build_api_deployment" {
  depends_on = [
    aws_api_gateway_integration.split_post_integration,
    aws_api_gateway_integration.openapi_get_integration
  ]

  rest_api_id = aws_api_gateway_rest_api.poly_build_api_gateway.id
  stage_name   = var.stage_name
}

resource "aws_lambda_permission" "allow_api_gateway_invoke" {
  action        = "lambda:InvokeFunction"
  function_name = var.poly_build_lambda_name
  principal     = "apigateway.amazonaws.com"
  statement_id  = "poly-build-api-permission"
  source_arn    = "arn:aws:execute-api:${var.aws_region}:${var.account_id}:${aws_api_gateway_rest_api.poly_build_api_gateway.id}/${var.stage_name}/ANY/*"
}
