# jproxy

**jproxy introduction**

> jproxy http proxy made by java built by netty v4.0,
you can visit real server's address via jproxy's address,just like squid,zproxy ect...

---
###jproxy's directory structure
**jproxy's** directory just like tomcat's or jmeter's 
- **bin**: to launch jproxy by `startup` shell, you can use `startup /etc/jproxy/config.json` to run this application by custom configuration file(it must be named as `config.json`)
- **lib**: this directory include the jar's dependented by jproxy, you ***should not ***move
- **conf** the place where jproxy's default configuration files lay
- **docs**documents about jproxy (sorry but I haven't finished it yet  — — | )
##jproxy's core function

#### memory cache
jproxy use memory to store http's post and get data(especially ***json***, ***form*** ***multipart data*** ect...).
jproxy's cache  module depend on **redis**.it can also save static resources such as ***html***, ***css***,***js***,and other image file. but now I shutdown the static resource storage because it has some bug on resolve resource's contentType. mayme someday I could figure it out...
####load balance
- **poll** 
jproxy will call the host that you set up into `config.json` one by one and again,you can set the filed `proxy_pass` like this:
``` 
  "proxy_pass":[
    {
      "host":"localhost",
      "port":8080,
      "weight":1
    },
    {
      "host":"localhost",
      "port":8080,
      "weight":1
    }
  ]
```
you can ignore filed `weight` first because it related to another mode
Filed `host` means real server's host (domain or ip).
Filed `port` is the real server's entrance that your client want to visit via jproxy.
Now,let's introduce the filed `weight`!!
- **weight poll** 
As you see, this is also a poll mode,but this will not visit real server one by one.
jproxy will fetch a real server's host from a container through server's `weight`'s value that you set up.
- **source ip hash**
I haven't test this mode yet but that'll be allright in theory. 
First jproxy fetch client's **ip**,
 next  use **hash** to comput a number, 
 then ***mode*** the real server's number, 
 finally we get a number in real server's number, so we can locate this client's ip hash to a certain real server.