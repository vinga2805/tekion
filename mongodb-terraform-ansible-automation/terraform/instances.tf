##################################################################################
#
# Instances
#
##################################################################################
resource "aws_instance" "mongo_instances" {
  count = var.quora
  ami = var.ami

  instance_type = var.mongo_instance_type

  tags = {
    Name = "mongo-${element(var.mongo_names, count.index)}"
  }

  security_groups = [aws_security_group.mongodb.name]

  ebs_block_device {
    volume_size           = var.mongo_volume_size
    volume_type           = "gp2"
    delete_on_termination = true
    device_name           = "/dev/sdb"
    encrypted             = var.encrypt_mongo_volume
  }

  key_name = aws_key_pair.deployer.key_name
}


resource "aws_instance" "mongo_arbiter_delayed" {
  count = length(var.arbiter_delayed)
  ami = var.ami

  instance_type = var.mongo_arbiter_instance_type

  tags = {
    Name = "mongo-${element(var.arbiter_delayed, count.index)}"
  }

  security_groups = [aws_security_group.mongodb.name]

  ebs_block_device {
    volume_size           = var.mongo_arbiter_volume_size
    volume_type           = "gp2"
    delete_on_termination = true
    device_name           = "/dev/sdb"
    encrypted             = var.encrypt_mongo_volume
  }

  key_name = aws_key_pair.deployer.key_name
}
