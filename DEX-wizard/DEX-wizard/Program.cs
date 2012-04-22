using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace DEX_wizard
{
    class Program
    {
        static void Main(string[] args)
        {
            bool running = true;

            // IPAddress ipAddress = Dns.Resolve(Dns.GetHostName()).AddressList[0];
            IPAddress ipAddress = IPAddress.Parse(args[0]);
            IPEndPoint ipLocalEndPoint = new IPEndPoint(ipAddress, int.Parse(args[1]));
            TcpListener server = new TcpListener(ipLocalEndPoint);
            server.Start();

            // listen for client
            Console.WriteLine("Listening for cilent . . .");
            TcpClient client = server.AcceptTcpClient();
            NetworkStream stream = client.GetStream();
            Console.WriteLine("Client connected. {0}", client.ToString());

            while (running)
            {
                string data = Console.ReadLine();
                if (data.StartsWith("quit"))
                {
                    running = false;
                    break;
                }
                Console.WriteLine("Sending: {0}", data);
                data += "\n\r";
                Byte[] buffer = Encoding.ASCII.GetBytes(data);
                stream.Write(buffer, 0, buffer.Length);
            }

            Console.WriteLine("exiting...");
            client.Close();
            server.Stop();
        }
    }
}
