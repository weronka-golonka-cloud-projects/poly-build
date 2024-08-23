data "aws_iam_policy_document" "dynamodb_table_access" {
  statement {
    actions = [
      "dynamodb:PutItem",
      "dynamodb:GetItem",
      "dynamodb:UpdateItem",
      "dynamodb:DeleteItem"
    ]
    effect = "Allow"
    resources = [
      var.building_sites_table_arn
    ]
  }
}