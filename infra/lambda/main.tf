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
      }
    ],
  })
}

resource "aws_iam_policy" "dynamodb_table_access" {
  name   = "poly-build-api-dynamodb-access"
  policy = data.aws_iam_policy_document.dynamodb_table_access.json
}

resource "aws_iam_role_policy_attachment" "dynamodb_table_access" {
  policy_arn = aws_iam_policy.dynamodb_table_access.arn
  role       = aws_iam_role.lambda_role.name
}

resource "aws_iam_role_policy_attachment" "cloudwatch" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
  role       = aws_iam_role.lambda_role.name
}

resource "aws_lambda_function" "poly_build_api" {
  function_name = "poly-build-api"
  runtime       = "java17"
  handler       = "com.weronka.golonka.PolyBuildApi"
  memory_size   = 512

  filename         = local.lambda_zip_path
  source_code_hash = filebase64sha256(local.lambda_zip_path)

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

output "poly_build_lambda_arn" {
  value = aws_lambda_function.poly_build_api.arn
}

output "poly_build_lambda_name" {
  value = aws_lambda_function.poly_build_api.function_name
}