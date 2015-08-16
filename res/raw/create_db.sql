create table if not exists contas(
  id integer primary key autoincrement, 
  descricao text not null, 
  vencimento numeric not null, 
  pagamento numeric null, 
  valor real not null, 
  tipo integer not null
);;

drop index if exists contas_vencimento_desc;;
create index contas_vencimento_desc on contas (vencimento desc);;

drop index if exists contas_tipo_desc;;
create index contas_tipo_desc on contas (tipo desc);;

drop view if exists receitas;;
create view receitas as 
select id, descricao, vencimento, pagamento, valor 
from contas 
where abs(tipo) = 0 
order by date(vencimento) desc;;

drop view if exists receitas_abertas;;
create view receitas_abertas as 
select * from receitas 
where pagamento is null;;

drop view if exists receitas_abertas_vencidas;;
create view receitas_abertas_vencidas as 
select * from receitas_abertas r 
where date(r.vencimento) < date('now');;

drop view if exists receitas_abertas_nao_vencidas;;
create view receitas_abertas_nao_vencidas as 
select * from receitas_abertas r 
where date(r.vencimento) >= date('now');;

drop view if exists receitas_pagas;;
create view receitas_pagas as 
select id, descricao, vencimento, pagamento, valor 
from receitas 
where not pagamento is null;;

drop view if exists despesas;;
create view despesas as 
select id, descricao, vencimento, pagamento, valor 
from contas 
where abs(tipo) = 1 
order by date(vencimento) desc;;

drop view if exists despesas_abertas;;
create view despesas_abertas as 
select * from despesas 
where pagamento is null;;

drop view if exists despesas_abertas_vencidas;;
create view despesas_abertas_vencidas as 
select * from despesas_abertas d 
where date(d.vencimento) < date('now');;

drop view if exists despesas_abertas_nao_vencidas;;
create view despesas_abertas_nao_vencidas as 
select * from despesas_abertas d 
where date(d.vencimento) >= date('now');;

drop view if exists despesas_pagas;;
create view despesas_pagas as 
select id, descricao, vencimento, pagamento, valor 
from despesas 
where not pagamento is null;;

create table if not exists contas_totais(
  receitas_abertas_vencidas real not null default 0,
  receitas_abertas_nao_vencidas real not null default 0,
  receitas_pagas real not null default 0,
  despesas_abertas_vencidas real not null default 0,
  despesas_abertas_nao_vencidas real not null default 0,
  despesas_pagas real not null default 0
);;

insert into contas_totais values(0, 0, 0, 0, 0, 0);;

drop view if exists receitas_abertas_vencidas_total;;
create view receitas_abertas_vencidas_total as 
select ifnull(sum(valor), 0) total from receitas_abertas_vencidas;;

drop view if exists receitas_abertas_nao_vencidas_total;;
create view receitas_abertas_nao_vencidas_total as 
select ifnull(sum(valor), 0) total from receitas_abertas_nao_vencidas;;

drop view if exists receitas_pagas_total;;
create view receitas_pagas_total as 
select ifnull(sum(valor), 0) total from receitas_pagas;;

drop view if exists despesas_abertas_vencidas_total;;
create view despesas_abertas_vencidas_total as 
select ifnull(sum(valor), 0) total from despesas_abertas_vencidas;;

drop view if exists despesas_abertas_nao_vencidas_total;;
create view despesas_abertas_nao_vencidas_total as 
select ifnull(sum(valor), 0) total from despesas_abertas_nao_vencidas;;

drop view if exists despesas_pagas_total;;
create view despesas_pagas_total as 
select ifnull(sum(valor), 0) total from despesas_pagas;;

drop trigger if exists totalizador_onInsert;;
create trigger totalizador_onInsert 
after insert on contas for each row 
begin
  update contas_totais set 
  receitas_abertas_vencidas = (select total from receitas_abertas_vencidas_total),
  receitas_abertas_nao_vencidas = (select total from receitas_abertas_nao_vencidas_total),
  receitas_pagas = (select total from receitas_pagas_total),
  despesas_abertas_vencidas = (select total from despesas_abertas_vencidas_total),
  despesas_abertas_nao_vencidas = (select total from despesas_abertas_nao_vencidas_total),
  despesas_pagas = (select total from despesas_pagas_total);
end;;

drop trigger if exists totalizador_onDelete;;
create trigger totalizador_onDelete 
after delete on contas for each row 
begin
  update contas_totais set 
  receitas_abertas_vencidas = (select total from receitas_abertas_vencidas_total),
  receitas_abertas_nao_vencidas = (select total from receitas_abertas_nao_vencidas_total),
  receitas_pagas = (select total from receitas_pagas_total),
  despesas_abertas_vencidas = (select total from despesas_abertas_vencidas_total),
  despesas_abertas_nao_vencidas = (select total from despesas_abertas_nao_vencidas_total),
  despesas_pagas = (select total from despesas_pagas_total);
end;;

drop trigger if exists totalizador_onUpdate;;
create trigger totalizador_onUpdate 
after update of valor on contas for each row 
begin
  update contas_totais set 
  receitas_abertas_vencidas = (select total from receitas_abertas_vencidas_total),
  receitas_abertas_nao_vencidas = (select total from receitas_abertas_nao_vencidas_total),
  receitas_pagas = (select total from receitas_pagas_total),
  despesas_abertas_vencidas = (select total from despesas_abertas_vencidas_total),
  despesas_abertas_nao_vencidas = (select total from despesas_abertas_nao_vencidas_total),
  despesas_pagas = (select total from despesas_pagas_total);
end;;