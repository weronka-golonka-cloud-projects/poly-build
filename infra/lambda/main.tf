resource "aws_iam_role" "lambda_role" {
  name = "poly_build_api_lambda_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      },
      {
        Effect = "Allow",
        Action = [
          "dynamodb:PutItem",
          "dynamodb:GetItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem"
        ],
        "Resource" : var.building_sites_table_arn
      }
    ],
  })
}

resource "aws_lambda_function" "poly_build_api" {
  function_name = "poly-build-api"
  runtime       = "java17"
  handler       = "com.weronka.golonka.PolyBuildApi"
  memory_size   = 512

  filename         = "../api/build/distributions/api.zip "
  #source_code_hash = filebase64sha256("../api/build/distributions/api.zip ")

  role = aws_iam_role.lambda_role.arn

  environment {
    variables = {
      LOCAL_AWS_CONFIG : null
    }
  }
}

resource "aws_lambda_permission" "allow_apigateway_invocation" {
  statement_id  = "poly_build_lambda_allow_apigateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.poly_build_api.function_name
  principal     = "apigateway.amazonaws.com"
}

resource "aws_lambda_permission" "allow_logs_invocation" {
  statement_id  = "log-permission"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.poly_build_api.function_name
  principal     = "logs.amazonaws.com"
  source_arn    = "arn:aws:logs:${var.aws_region}:${var.account_id}:log-group:/aws/lambda/poly-build-api"
}

output "poly_build_lambda_arn" {
  value = aws_lambda_function.poly_build_api.arn
}

output "poly_build_lambda_name" {
  value = aws_lambda_function.poly_build_api.function_name
}