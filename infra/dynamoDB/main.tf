resource "aws_dynamodb_table" "building_sites" {
  name           = "BuildingSites"
  read_capacity  = 5
  write_capacity = 5
  hash_key       = "id"

  attribute {
    name = "id"
    type = "S"
  }
}

output "building_sites_arn" {
  value = aws_dynamodb_table.building_sites.arn
}