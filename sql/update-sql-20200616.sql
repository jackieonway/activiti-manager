alter table act_id_group
	add TENANT_ID varchar(255) null comment '租户id';

alter table act_id_info
	add TENANT_ID varchar(255) null comment '租户id';

alter table act_id_user
	add TENANT_ID varchar(255) null comment '租户id';

alter table act_id_membership
	add TENANT_ID varchar(255) null comment '租户id';