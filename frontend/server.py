import os
import cherrypy
from tasks import Task
from run_trigger import download_website

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
    cherrypy.config.update({'global': {'environment' : 'production'}})
    cherrypy.log.screen = True
    
    task = Task(cherrypy.engine, lambda: download_website("http://localhost:8081/StockSim-1.0-SNAPSHOT/api/exchange-task"), period=300, init_delay=60, repeat_on_close=False)
    task.subscribe()
    
    cherrypy.tree.mount(StaticServer(), '/', dict())
    cherrypy.engine.start()
    cherrypy.engine.block()