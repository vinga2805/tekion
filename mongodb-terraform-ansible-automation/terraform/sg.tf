##################################################################################
#
# VPC and security group
#
##################################################################################
resource "aws_default_vpc" "default" {
  tags = {
    Name = "Default VPC"
  }
}

resource "aws_security_group" "mongodb" {
  vpc_id = aws_default_vpc.default.id

  # allow all from Devops home
  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["${var.home_ip}/32"]
  }

  # allow 80 for letsencrypt (will be handled with firewalld)
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # allow 443 for letsencrypt (will be handled with firewalld)
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # allow monitoring port from all members to all members
  ingress {
    from_port   = 42000
    to_port     = 42005
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] 
  }

  # allow monitoring server port from all members to all members
  ingress {
    from_port   = 8443
    to_port     = 8443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # allow 27017 from all members to all members
  ingress {
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

