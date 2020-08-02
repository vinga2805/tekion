resource "aws_eip" "mongo_eips" {
   count = var.quora
   instance   = "${element(aws_instance.mongo_instances.*.id,count.index)}"
   tags = {
    Name = "eip-${count.index + 1}"
  }
}

resource "aws_eip" "mongo_arbiter_delayed" {
   count = length(var.arbiter_delayed) 
   instance   = "${element(aws_instance.mongo_arbiter_delayed.*.id,count.index)}"
   tags = {
    Name = "eip-${element(var.arbiter_delayed,count.index + 1)}"
  }
}
#locals {
#  trusted_mongo = [
#    "${aws_eip.mongo_eips.*.public_ip}/32",
#    "${aws_eip.mongo_arbiter_delayed.*.public_ip}/32"
#  ]
#}
