package com.klarshift.veytonhooker

class VeytonHooker {
	private File basePath
	private List<String> filters
	private LinkedList<File> files = new LinkedList<File>()
	private Map<String,List<Hookpoint>> hookPoints = [:]
	
	
	public VeytonHooker(File basePath, String filter){
		this.basePath = basePath		
		this.filters = filter.split(" ")
	}
	
	public void extract(){
		files.add(basePath)
		
		while(files.size() > 0){
			File next = files.removeFirst()
			process(next)
		}
	}	
	
	private void process(File file){
		if(file.isDirectory()){
			file.eachFile { File f ->
				String name = f.name
				if(f.isDirectory() || (name.endsWith(".php") || name.endsWith(".html")))
					files.add(f)
			}
			return			
		}
							
		int lc = 0
		
		String path = file.getAbsolutePath()
		
		
		file.eachLine { String line ->
			lc++
			
			/*if(line.contains('PluginCode'))
				println line.substring(0, Math.min(line.size()-1, 100))*/ 
			
			def g = { pattern, type ->
				def r = line =~ pattern
				if(r){
					String key = r[0][1]
					
					if(filters.size()){
						for(String f : filters){
							if(key.contains(f) == false){
								return
							}
						}
						
					}
					
					Hookpoint hp = new Hookpoint()
					hp.file = file
					hp.key = key
					hp.line = lc
					hp.type = type
					
					if(!hookPoints[path])
						hookPoints[path] = []
					
					hookPoints[path] << hp
					
				}
			}
			
			g(/hook.+?key=([a-zA-Z_]+)/, 'template')
			g(/PluginCode\(['\"](.+?)['\"]\)/, 'php')				
		}
				
	}
	
	public void printResults(){
		int total = 0
		if(hookPoints.size() > 0){
			
			
			hookPoints.each{ hpe ->
				
				String path = hpe.key
				String shortName = path.substring(basePath.getAbsolutePath().size())
				println "[ $shortName ]"
				hpe.value.each{ Hookpoint hp ->		
					total++
					println "\t${hp}"
				}
				println ""
			}
			
		}
		
		println "FOUND ${total} hook points in ${hookPoints.size()} different files."
	}

	static main(args) {
		File basePath
		
		// file
		if(args.length > 0){
			basePath = new File(args[0])
		}else{
			basePath = new File("/home/timo/xt")			
		}
		
		// filter
		String filter = ""
		if(args.length > 1){
			filter = args[1]
		}
		
		def hooker = new VeytonHooker(basePath, filter)
		hooker.extract()
		hooker.printResults()
	}

}
