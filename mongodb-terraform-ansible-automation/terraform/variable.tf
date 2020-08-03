#--------------------------------------------------------------
# General
#--------------------------------------------------------------
variable "region" {
  description = "The AWS region to create things in."
  default     = "ap-south-1"
}

variable "public_key_path" {
  description = "The path of the public key file to use"
  default     = "~/.ssh/id_rsa.pub"
}

variable "ami" {
  description = "The AMI to use. Be Carreful. Only Ubuntu 18.04 has been tested"
  default = "ami-02d55cb47e83a99a0"
}

variable "quora" {
  description = "Please enter the quora count"
  default = "5"
}
variable "mongo_names" {
  description = "Please mention the list of arbiter and delayed nodes"
  type        = list
  default     = ["master","slave1","slave2","slave3","slave4"]
}
#--------------------------------------------------------------
# Network
#--------------------------------------------------------------
variable "home_ip" {
  description = "The Devops home IP."
  default     = "15.207.100.254"
}

#--------------------------------------------------------------
# Instances
#--------------------------------------------------------------
variable "mongo_instance_type" {
  description = "Type of instance to use for mongodb nodes"
  default     = "t3a.medium"
}

variable "arbiter_delayed" {
  description = "Please mention the list of arbiter and delayed nodes"
  type        = list
  default     = ["arbiter","delayed"]
}
variable "mongo_arbiter_instance_type" {
  description = "Type of instance to use for mongodb arbiter node"
  default     = "t3a.medium"
}

variable "mongo_volume_size" {
  description = "Size in GB of the mongodb volume"
  default     = 30
}

variable "mongo_arbiter_volume_size" {
  description = "Size in GB of the mongodb volume for arbiter"
  default     = 30
}

variable "encrypt_mongo_volume" {
  description = "Define whereas the mongodb volume needs to be encrypted. If true ansible deployment can skip 'encrypt-rest' plays"
  default      = false
}
variable "zone_id" {
  description = "Define domain zone id"
  default = "Z0537173201HISIGMS6CD"
}
