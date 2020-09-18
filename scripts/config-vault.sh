#!/usr/bin/zsh
case $1 in
    dev)
        echo "running in dev environment"
        export VAULT_ADDR='http://127.0.0.1:8200'
        vault login $VAULT_TOKEN
        ;;
    prod)
        echo "running in prod environment"
        unset VAULT_TOKEN
        eval `gpg -d ~/dokumente/secret/passwords/scripts/kubemaster01_login.sh.gpg`
        #export VAULT_ADDR='https://127.0.0.1:8200'
        #vault login $VAULT_TOKEN
        ;;
    *)
        echo "unkown environment $1"
        ;;
esac

vault kv put secret/userservice cassandra.contactpoints=cassandra01.kubernetes.ka.xcore.net cassandra.keyspace=userservice cassandra.localdatacenter=datacenter1
