variable "aws_region" {
  type = string
}

variable "account_id" {
  type = string
}

variable "poly_build_lambda_arn" {
  type = string
  description = "ARN of the Poly Build Lambda function"
}

variable "poly_build_lambda_name" {
  type = string
  description = "Name of the Poly Build Lambda function"
}

variable "stage_name" {
  type = string
  description = "Name of the stage for API Gateway"
}