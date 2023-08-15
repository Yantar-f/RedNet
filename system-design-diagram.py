from diagrams import Diagram, Cluster, Edge
from diagrams.onprem.client import User
from diagrams.onprem.database import Postgresql, Cassandra
from diagrams.onprem.inmemory import Redis
from diagrams.onprem.queue import RabbitMQ
from diagrams.programming.framework import Spring

with Diagram(
    name="RedNet System Design",
    filename="system-design-diagram",
    outformat="png",
    show=True,
    direction="TB"
):
    client_request_edge = Edge(color="#0ECC00")
    client_proxied_request_edge = Edge(color="#0ECC00", style="dashed")
    server_to_server_edge = Edge(color="#00DEDB")

    client = User("spa")
    agw = Spring("agw")

    with Cluster("External Services"):
        with Cluster("Auth Management Service"):
            auth_logic_server = Spring("bl server")
            accounts_db = Postgresql("accounts")
            refresh_tokens_db = Redis("refresh tokens")

        with Cluster("Sse Service"):
            sse_logic_server = Spring("bl server")

        with Cluster("Chat Service"):
            chat_logic_server = Spring("bl server")
            messages_db = Cassandra("messages")

    with Cluster("Internal Services"):
        with Cluster("Notification Producer Service"):
            notification_server = Spring("bl server")
            notification_queue = RabbitMQ("notification queue")

        with Cluster("Session Service"):
            session_logic_server = Spring("bl server")
            sessions_db = Cassandra("sessions")

    client_service_group = [
        auth_logic_server,
        sse_logic_server,
        chat_logic_server
    ]

    notifiable_service_group = [
        auth_logic_server,
        session_logic_server,
        chat_logic_server
    ]

    client >> client_request_edge >> agw >> client_proxied_request_edge >> client_service_group
    auth_logic_server >> server_to_server_edge >> session_logic_server
    auth_logic_server >> accounts_db
    auth_logic_server >> refresh_tokens_db
    chat_logic_server >> messages_db
    session_logic_server >> sessions_db
    notifiable_service_group >> server_to_server_edge >> notification_server >> notification_queue
    notification_queue >> server_to_server_edge >> sse_logic_server
