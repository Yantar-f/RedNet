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
    client_request_edge = Edge(color="#0ECC00")
    client_proxied_request_edge = Edge(color="#0ECC00", style="dashed")
    server_to_server_edge = Edge(color="#00DEDB")
    server_to_server_proxied_edge = Edge(color="#00DEDB", style="dashed")
    config_edge = Edge(color="orange")

    client = User("spa")

    with Cluster("External Services"):
        agw = Spring("agw")

        with Cluster("Auth Management Service"):
            auth_logic_server = Spring("bl server")
            accounts_db = Postgresql("accounts")
            registrations_db = Redis("registrations")

            auth_logic_server >> [accounts_db, registrations_db]

        with Cluster("Sse Service"):
            sse_logic_server = Spring("bl server")

        with Cluster("Chat Service"):
            chat_logic_server = Spring("bl server")
            messages_db = Cassandra("messages")

            chat_logic_server >> messages_db

    with Cluster("Internal Services"):
        internal_agw = Spring("internal agw")
        config_service = Spring("config")

        with Cluster("Event Producer Service"):
            event_logic_server = Spring("bl server")
            event_queue = RabbitMQ("event queue")

            event_logic_server >> event_queue

        with Cluster("Session Service"):
            session_logic_server = Spring("bl server")
            sessions_db = Redis("sessions")

            session_logic_server >> sessions_db

        external_service_group = [
            auth_logic_server,
            sse_logic_server,
            chat_logic_server
        ]

        internal_service_group = [
            session_logic_server,
            event_logic_server
        ]

        services = internal_service_group + external_service_group

        notifiable_service_group = [
            auth_logic_server,
            chat_logic_server
        ]

        configurable_service_group = [
            auth_logic_server,
            session_logic_server,
            sse_logic_server,
            chat_logic_server,
            agw,
            internal_agw,
            event_logic_server
        ]

    client >> client_request_edge >> agw >> client_proxied_request_edge >> external_service_group
    external_service_group >> server_to_server_edge >> internal_agw >> server_to_server_proxied_edge >> internal_service_group
    event_queue >> server_to_server_edge >> sse_logic_server
    configurable_service_group >> config_edge >> config_service
