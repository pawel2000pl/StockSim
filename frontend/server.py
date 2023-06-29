import os
import cherrypy

class StaticServer(object):
    @cherrypy.expose
    def default(self, *args, **kwargs):
        if len(args) == 0:
            args = ["index.html"]
        path = os.path.join(os.path.abspath("static"), *args)
        if os.path.exists(path):
            return cherrypy.lib.static.serve_file(path)
        else:
            raise cherrypy.NotFound()

if __name__ == '__main__':    
    cherrypy.config.update({
        'server.socket_host': '0.0.0.0',
        'server.socket_port': 8080
    })
    cherrypy.quickstart(StaticServer())