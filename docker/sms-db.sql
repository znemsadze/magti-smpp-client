
create table  sms_state(
    id bigserial primary key,
    name varchar(200),
    description varchar(200)
);

insert into sms_state (name, id) values ('PRE_SUBMITTED ',   7);
insert into sms_state (name, id) values ('SUBMITTED',   2);
insert into sms_state (name, id) values ('SENT',   3);
insert into sms_state (name, id) values ('DELIVERED',   4);
insert into sms_state (name, id) values ('BLOCKED',   5);
insert into sms_state (name, id) values ('SMS_CENTER_FAIL',8);

create table sms_queue(
    id bigserial primary key ,
    phone_number varchar(15),
    send_date timestamp,
    message_id varchar(30),
    delivery_date timestamp,
    param varchar(2000),
    sms_text varchar(2000),
    sender varchar(50),
    state_id int4,
    is_geo int4,
    no_delivery int4
);

insert into sms_queue(id, phone_number, send_date, message_id, delivery_date, param, sms_text, sender,  state_id, is_geo, no_delivery)
values (21,'599443000',null,null,null,null,'vazrovneb mashasadame varsebob rene','test_a2p',1,0,0);

insert into sms_queue(id, phone_number, send_date, message_id, delivery_date, param, sms_text, sender,  state_id, is_geo, no_delivery)
values (22,'599443000',null,null,null,null,'zamtaria sicivea shemodgoma midis meshinia sicivea zamtarshi rom ' ||
                                           'icis sharshan ufro sashineli civi qari qroda magram sharshan chemTan' ||
                                           ' iyav sul ar meshinoda zamTaria sicivea Semodgoma midismeSinia sicivisa', 'test_a2p',1,0,0);



insert into sms_queue(id, phone_number, send_date, message_id, delivery_date, param, sms_text, sender,  state_id, is_geo, no_delivery)
values (19,'599443000',null,null,null,null,'ვაზროვნებ მაშასადამე სდფსდსდ სასდასდ','test_a2p',1,1,0);

insert into sms_queue(id, phone_number, send_date, message_id, delivery_date, param, sms_text, sender,  state_id, is_geo, no_delivery)
values (20,'599443000',null,null,null,null,'ზამთარია სიცივეა ცივი ქარი ' ||
                                           'ქრის მეშინია სიცივისა ზამთარში რომ იცის შარშან უფრო საშინელი' ||
                                           ' ცივი ქარი ქროდა', 'test_a2p',1,1,0);
SELECT *from sms_queue order by id desc;


