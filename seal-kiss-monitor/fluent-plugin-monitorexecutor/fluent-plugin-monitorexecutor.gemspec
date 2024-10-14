# encoding: utf-8
$:.push File.expand_path('../lib', __FILE__)

Gem::Specification.new do |gem|
  gem.name        = "fluent-plugin-monitorexecutor"
  gem.description = "monitorexecutor filter plugin for Fluentd monitor api"
  gem.homepage    = "https://github.com/seal90/seal-kiss-spring-initializr/seal-kiss-monitor/fluent-plugin-monitorexecutor"
  gem.summary     = gem.description
  gem.version     = File.read("VERSION").strip
  gem.authors     = ["seal"]
  gem.email       = "578935869@qq.com"
  #gem.platform    = Gem::Platform::RUBY
  gem.files       = ['lib/fluent/plugin/filter_monitorexecutor.rb']
  # gem.test_files  = `ls . | grep test,spec,features`.split("\n")
  # gem.executables = `git ls-files -- bin/*`.split("\n").map{ |f| File.basename(f) }
  gem.require_paths = ['lib']
  gem.license = "Apache-2.0"

  gem.add_dependency "fluentd", [">= 1.7.0", "< 2"]
  gem.add_development_dependency "rake", ">= 0.9.2"
end
