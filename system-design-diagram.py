from diagrams import Diagram, Cluster, Edge
from diagrams.onprem.client import User
from diagrams.onprem.database import Postgresql, Cassandra
from diagrams.onprem.inmemory import Redis
from diagrams.onprem.queue import RabbitMQ
from diagrams.programming.framework import Spring

with (Diagram(
    name="RedNet System Design",
    filename="system-design-diagram",
    outformat="png",
    show=True,
    direction="TB"
)):
    server_to_server_edge = Edge(color="#00DEDB")
    server_to_server_proxied_edge = Edge(color="#00DEDB", style="dashed")
    config_edge = Edge(color="#8AD618")

    client = User("spa")

    with Cluster("External Services"):
        agw = Spring("agw")

        with Cluster("Auth Management Service"):
            auth_logic_server = Spring("bl server")

        with Cluster("Sse Service"):
            sse_logic_server = Spring("bl server")

        with Cluster("Chat Service"):
            chat_logic_server = Spring("bl server")

    with Cluster("Internal Services"):
        internal_agw = Spring("internal agw")
        config_service = Spring("config")
        service_discovery = Spring("service discovery")

        with Cluster("Registration Service"):
            registration_logic_server = Spring("bl server")
            registrations_db = Redis("registrations")

            registration_logic_server >> registrations_db

        with Cluster("Account Service"):
            account_logic_server = Spring("bl server")
            accounts_db = Postgresql("accounts")

            account_logic_server >> accounts_db

        with Cluster("Event Producer Service"):
            event_logic_server = Spring("bl server")
            event_queue = RabbitMQ("event queue")

            event_logic_server >> event_queue

        with Cluster("Session Service"):
            session_logic_server = Spring("bl server")
            sessions_db = Cassandra("sessions")

            session_logic_server >> sessions_db

        with Cluster("Message Service"):
            message_bl_server = Spring("bl server")
            message_db = Cassandra("messages")

            message_bl_server >> message_db

        with Cluster("Conversation Service"):
            conversation_logic_server = Spring("bl server")
            conversations_db = Cassandra("conversations")

            conversation_logic_server >> conversations_db

        external_service_group = [
            auth_logic_server,
            sse_logic_server,
            chat_logic_server
        ]

        internal_service_group = [
            registration_logic_server,
            account_logic_server,
            session_logic_server,
            event_logic_server,
            message_bl_server,
            conversation_logic_server
        ]

        services = internal_service_group + external_service_group

        notifiable_service_group = [
            auth_logic_server,
            chat_logic_server
        ]

        configurable_service_group = internal_service_group + external_service_group + [agw, internal_agw]
        discovered_service_group = configurable_service_group

    client >> server_to_server_edge >> agw >> server_to_server_proxied_edge >> external_service_group
    external_service_group >> server_to_server_edge >> internal_agw >> server_to_server_proxied_edge >> internal_service_group
    event_queue >> server_to_server_edge >> sse_logic_server
    configurable_service_group >> config_edge >> config_service
    discovered_service_group >> config_edge >> service_discovery
