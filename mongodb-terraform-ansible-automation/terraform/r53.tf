resource "aws_route53_record" "dns" {
  count   = var.quora
  zone_id = var.zone_id
  name    = "mongo-${element(var.mongo_names,count.index)}.vinga.tk"
  type    = "A"
  ttl     = "300"
  records = ["${element(aws_eip.mongo_eips.*.public_ip, count.index)}"]
}
resource "aws_route53_record" "dns-arb-del" {
  count   = length(var.arbiter_delayed)
  zone_id = var.zone_id
  name    = "mongo-${element(var.arbiter_delayed, count.index)}.vinga.tk"
  type    = "A"
  ttl     = "300"
  records = ["${element(aws_eip.mongo_arbiter_delayed.*.public_ip, count.index)}"]
}
