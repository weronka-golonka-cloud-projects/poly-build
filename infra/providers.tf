terraform {
  backend "s3" {
    bucket         = "poly-build-tf-state"
    key            = "terraform.tfstate"
    region         = "eu-north-1"
    dynamodb_table = "TerraformLocks"
    encrypt        = true
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "eu-north-1"
}