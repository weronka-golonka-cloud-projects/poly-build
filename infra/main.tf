module "building_sites_table" {
  source = "./dynamoDB"
}

module "poly_build_api_lambda" {
  source = "./lambda"

  aws_region = "eu-north-1"
  account_id = ""
  building_sites_table_arn = module.building_sites_table.building_sites_arn
}

module "poly_build_api_gateway" {
  source = "./api-gateway"

  aws_region = "eu-north-1"
  account_id = ""
  poly_build_lambda_arn = module.poly_build_api_lambda.poly_build_lambda_arn
  poly_build_lambda_name = module.poly_build_api_lambda.poly_build_lambda_name
  stage_name = "dev"
}