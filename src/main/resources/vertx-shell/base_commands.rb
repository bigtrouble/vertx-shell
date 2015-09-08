require 'vertx-shell/command'
require 'vertx/util/utils.rb'
# Generated from io.vertx.ext.shell.command.BaseCommands
module VertxShell
  #  @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
  class BaseCommands
    # @private
    # @param j_del [::VertxShell::BaseCommands] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxShell::BaseCommands] the underlying java delegate
    def j_del
      @j_del
    end
    # @return [::VertxShell::Command]
    def self.verticle_ls
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:verticle_ls, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling verticle_ls()"
    end
    # @return [::VertxShell::Command]
    def self.verticle_deploy
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:verticle_deploy, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling verticle_deploy()"
    end
    # @return [::VertxShell::Command]
    def self.verticle_undeploy
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:verticle_undeploy, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling verticle_undeploy()"
    end
    # @return [::VertxShell::Command]
    def self.verticle_factories
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:verticle_factories, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling verticle_factories()"
    end
    # @return [::VertxShell::Command]
    def self.server_ls
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:server_ls, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling server_ls()"
    end
    # @return [::VertxShell::Command]
    def self.local_map_get
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:local_map_get, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling local_map_get()"
    end
    # @return [::VertxShell::Command]
    def self.local_map_put
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:local_map_put, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling local_map_put()"
    end
    # @return [::VertxShell::Command]
    def self.local_map_rm
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:local_map_rm, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling local_map_rm()"
    end
    # @return [::VertxShell::Command]
    def self.bus_send
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:bus_send, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling bus_send()"
    end
    # @return [::VertxShell::Command]
    def self.bus_tail
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:bus_tail, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling bus_tail()"
    end
    # @return [::VertxShell::Command]
    def self.fs_cd
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:fs_cd, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling fs_cd()"
    end
    # @return [::VertxShell::Command]
    def self.fs_pwd
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:fs_pwd, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling fs_pwd()"
    end
    # @return [::VertxShell::Command]
    def self.fs_ls
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:fs_ls, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling fs_ls()"
    end
    # @return [::VertxShell::Command]
    def self.sleep
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:sleep, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling sleep()"
    end
    # @return [::VertxShell::Command]
    def self.echo
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:echo, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling echo()"
    end
    # @return [::VertxShell::Command]
    def self.help
      if !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtShellCommand::BaseCommands.java_method(:help, []).call(),::VertxShell::Command)
      end
      raise ArgumentError, "Invalid arguments when calling help()"
    end
  end
end
