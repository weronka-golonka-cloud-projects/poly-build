module "building_sites_table" {
  source = "./dynamoDB"
}

module "poly_build_api_lambda" {
  source = "./lambda"

  aws_region = "eu-north-1"
  building_sites_table_arn = module.building_sites_table.building_sites_arn
}


module "poly_build_api_gateway_v2" {
  source = "./api-gatewayv2"

  poly_build_lambda_arn = module.poly_build_api_lambda.poly_build_lambda_arn
}