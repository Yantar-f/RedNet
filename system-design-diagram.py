from diagrams import Diagram, Cluster, Edge
from diagrams.c4 import Person, SystemBoundary, Container, Database
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

    client = Person(
        name="Client",
        description="Single-Page-Application"
    )

    with Cluster("External Services"):
        agw = Container(
            name="API gateway",
            technology="Spring cloud gateway",
            description="validates auth tokens, routes and load balance requests"
        )

        auth_logic_server = Container(
            name="Auth management service",
            technology="Spring boot",
            description="Service for management authentication and authorization"
        )

        sse_logic_server = Container(
            name="SSE service",
            technology="Spring boot",
            description="Service for send one-directional event notifications"
        )

        chat_logic_server = Container(
            name="Chat service",
            technology="Spring boot",
            description="Service for providing chat features"
        )

    with Cluster("Internal Services"):
        internal_agw = Container(
            name="API Gateway",
            technology="Spring cloud gateway",
            description="routes and load balance requests"
        )

        config_service = Container(
            name="Config server",
            technology="Spring cloud config",
            description="Contains general configuration for all system"
        )

        service_discovery = Container(
            name="Service discovery",
            technology="Eureka server",
            description="Service for storing and registration active services"
        )

        event_queue = Container(
            name="Event queue",
            technology="RabbitMQ",
            description="MQ for system event notifications"
        )

        with Cluster(""):
            registration_logic_server = Container(
                name="Registration service",
                technology="Spring boot",
                description="Service for storing web-app registrations"
            )

            registrations_db = Database(
                name="Registrations",
                technology="Redis",
            )

            registration_logic_server >> registrations_db

            account_logic_server = Container(
                name="Account service",
                technology="Spring boot",
                description="Service for storing accounts general info"
            )

            accounts_db = Database(
                name="Accounts",
                technology="PostgreSQL"
            )

            account_logic_server >> accounts_db

            session_logic_server = Container(
                name="Session service",
                technology="Spring boot",
                description="Service for storing sessions"
            )

            sessions_db = Database(
                name="Sessions",
                technology="CassandraDB"
            )

            session_logic_server >> sessions_db

            message_bl_server = Container(
                name="Message service",
                technology="Spring boot",
                description="Service for storing sessions"
            )

            message_db = Database(
                name="Messages",
                technology="CassandraDB"
            )

            message_bl_server >> message_db

            conversation_logic_server = Container(
                name="Conversation Service",
                technology="Spring boot",
                description="Service for storing info about conversations"
            )

            conversations_db = Database(
                name="Conversations info",
                technology="CassandreDB"
            )

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
    internal_service_group >> event_queue >> server_to_server_edge >> sse_logic_server
    configurable_service_group >> config_edge >> config_service
    discovered_service_group >> config_edge >> service_discovery
