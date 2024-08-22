variable "aws_region" {
  type = string
}

variable "account_id" {
  type = string
}

variable "building_sites_table_arn" {
  type = string
  description = "ARN of the BuildingSites DynamoDB table"
}