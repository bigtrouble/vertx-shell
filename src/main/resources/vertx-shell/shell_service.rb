require 'vertx-shell/shell_server'
require 'vertx-shell/command_registry'
require 'vertx/vertx'
require 'vertx/util/utils.rb'
# Generated from io.vertx.ext.shell.ShellService
module VertxShell
  #  The shell service, provides a remotely accessible shell available via Telnet or SSH according to the
  #  {Hash} configuration.
  class ShellService
    # @private
    # @param j_del [::VertxShell::ShellService] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxShell::ShellService] the underlying java delegate
    def j_del
      @j_del
    end
    # @param [::Vertx::Vertx] vertx 
    # @param [Hash] options 
    # @return [::VertxShell::ShellService]
    def self.create(vertx=nil,options=nil)
      if vertx.class.method_defined?(:j_del) && !block_given? && options == nil
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShell::ShellService.java_method(:create, [Java::IoVertxCore::Vertx.java_class]).call(vertx.j_del),::VertxShell::ShellService)
      elsif vertx.class.method_defined?(:j_del) && options.class == Hash && !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShell::ShellService.java_method(:create, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxExtShell::ShellServiceOptions.java_class]).call(vertx.j_del,Java::IoVertxExtShell::ShellServiceOptions.new(::Vertx::Util::Utils.to_json_object(options))),::VertxShell::ShellService)
      end
      raise ArgumentError, "Invalid arguments when calling create(vertx,options)"
    end
    #  Start the shell service, this is an asynchronous start.
    # @yield handler for getting notified when service is started
    # @return [void]
    def start
      if !block_given?
        return @j_del.java_method(:start, []).call()
      elsif block_given?
        return @j_del.java_method(:start, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling start()"
    end
    #  @return the command registry of this service
    # @return [::VertxShell::CommandRegistry]
    def command_registry
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(@j_del.java_method(:commandRegistry, []).call(),::VertxShell::CommandRegistry)
      end
      raise ArgumentError, "Invalid arguments when calling command_registry()"
    end
    #  @return the shell server
    # @return [::VertxShell::ShellServer]
    def server
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(@j_del.java_method(:server, []).call(),::VertxShell::ShellServer)
      end
      raise ArgumentError, "Invalid arguments when calling server()"
    end
    #  Stop the shell service, this is an asynchronous start.
    # @yield handler for getting notified when service is stopped
    # @return [void]
    def stop
      if !block_given?
        return @j_del.java_method(:stop, []).call()
      elsif block_given?
        return @j_del.java_method(:stop, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
      end
      raise ArgumentError, "Invalid arguments when calling stop()"
    end
  end
end
